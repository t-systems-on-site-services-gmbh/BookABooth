import { defineComponent } from 'vue';
import { useAlertService } from '@/shared/alert/alert.service';
import CompanyService from './waitinglist.service';
import SystemService from '@/entities/system/system.service';

export default defineComponent({
  name: 'NotifyWaitingListComponent',
  data() {
    return {
      isEnabled: false,
      waitingListEntries: [] as Array<{ id: number; name: string; mail: string; waitingList: boolean }>,
      filteredEntries: [] as Array<{ id: number; name: string; mail: string; waitingList: boolean }>,
      searchQuery: '',
      companyService: new CompanyService(),
      systemService: new SystemService(),
      alertService: useAlertService(),
    };
  },
  created() {
    this.fetchSystemStatus();
    this.loadWaitingListData();
  },
  methods: {
    async fetchSystemStatus() {
      try {
        const response = await this.systemService.retrieve();
        if (response.data) {
          this.isEnabled = response.data.enabled;
        }
      } catch (error) {
        console.error('Fehler beim Abrufen des Systemstatus:', error);
        this.alertService.showError('Fehler beim Abrufen des Systemstatus.');
      }
    },
    async loadWaitingListData() {
      try {
        const response = await this.companyService.retrieve();
        this.waitingListEntries = response.data.map((entry: any) => ({
          id: entry.id,
          name: entry.name,
          mail: entry.mail,
          waitingList: entry.waitingList,
        }));
        this.filteredEntries = this.waitingListEntries;
      } catch (error) {
        console.error('Fehler beim Laden der Warteliste:', error);
        this.alertService.showError('Fehler beim Laden der Warteliste.');
      }
    },
    async notifyWaitingList() {
      try {
        await this.companyService.notifyWaitingList();
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
        await this.companyService.updateWaitingListStatus(entry.id, updatedStatus);
        entry.waitingList = updatedStatus;
        this.alertService.showInfo(`Warteliste-Status für ${entry.name} wurde auf ${updatedStatus ? 'Ja' : 'Nein'} gesetzt.`);
      } catch (error) {
        console.error('Fehler beim Aktualisieren des Warteliste-Status:', error);
        this.alertService.showError('Der Warteliste-Status konnte nicht aktualisiert werden. Bitte versuchen Sie es erneut.');
      }
    },
  },
});
