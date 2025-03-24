import { defineComponent, inject, onMounted, ref, type Ref, computed } from 'vue';

import { useAlertService } from '@/shared/alert/alert.service';
import SystemService from '@/entities/system/system.service';
import WaitingListService from './waitinglist.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'NotifyWaitingListComponent',

  setup() {
    const systemService = inject('systemService', () => new SystemService());
    const alertService = inject('alertService', () => useAlertService(), true);
    const waitingListService = inject('waitingListService', () => new WaitingListService());

    const isEnabled = ref(false);
    const isFetching = ref(false);
    const searchQuery = ref('');
    const waitingListEntries: Ref<Array<{ id: number; name: string; mail: string; waitingList: boolean }>> = ref([]);
    const filteredEntries: Ref<Array<{ id: number; name: string; mail: string; waitingList: boolean }>> = ref([]);

    const fetchSystemStatus = async () => {
      try {
        const response = await systemService().retrieve();
        if (response.data) {
          isEnabled.value = response.data.enabled;
        }
      } catch (error) {
        console.error('Fehler beim Abrufen des Systemstatus:', error);
        alertService.showError('Fehler beim Abrufen des Systemstatus.');
      }
    };

    const loadWaitingListData = async () => {
      try {
        const response = await waitingListService().retrieve();
        waitingListEntries.value = response.data.map((entry: any) => ({
          id: entry.id,
          name: entry.name,
          mail: entry.mail,
          waitingList: entry.waitingList,
        }));
        filteredEntries.value = waitingListEntries.value;
      } catch (error) {
        console.error('Fehler beim Laden der Warteliste:', error);
        alertService.showError('Fehler beim Laden der Warteliste.');
      }
    };

    onMounted(async () => {
      await fetchSystemStatus();
      await loadWaitingListData();
    });

    return {
      systemService,
      alertService,
      waitingListService,
      isEnabled,
      isFetching,
      searchQuery,
      waitingListEntries,
      filteredEntries,
      loadWaitingListData,
    };
  },
  methods: {
    async notifyWaitingList() {
      try {
        await this.waitingListService().notifyWaitingList();
        this.alertService.showSuccess('E-Mails wurden erfolgreich an die Warteliste gesendet.');
      } catch (error) {
        console.error('Fehler beim Senden der Benachrichtigungen:', error);
        this.alertService.showError('Fehler beim Senden der Benachrichtigungen. Bitte versuchen Sie es erneut.');
      }
    },
    handleSyncList() {
      this.loadWaitingListData();
    },
    async toggleWaitingListStatus(entry: any) {
      try {
        const updatedStatus = !entry.waitingList;
        console.log(`Aktualisiere Status für ID ${entry.id}: ${updatedStatus}`);
        await this.waitingListService().updateWaitingListStatus(entry.id, updatedStatus);
        entry.waitingList = updatedStatus;
        this.alertService.showInfo(`Warteliste-Status für ${entry.name} wurde auf ${updatedStatus ? 'Ja' : 'Nein'} gesetzt.`);
      } catch (error) {
        console.error('Fehler beim Aktualisieren des Warteliste-Status:', error);
        this.alertService.showError('Der Warteliste-Status konnte nicht aktualisiert werden. Bitte versuchen Sie es erneut.');
      }
    },
  },
});
