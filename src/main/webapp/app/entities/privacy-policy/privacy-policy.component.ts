import { defineComponent, inject, ref, type Ref } from 'vue';
import axios from 'axios';
import { useAlertService } from '@/shared/alert/alert.service';
import { type IPrivacyPolicy } from '@/shared/model/privacy-policy.model';

export default defineComponent({
  name: 'PrivacyPolicy',
  setup() {
    const alertService = inject('alertService', () => useAlertService(), true);
    const privacyPolicies: Ref<IPrivacyPolicy[]> = ref([]);

    const createNewPrivacyPolicy = async () => {
      try {
        const response = await axios.post('api/privacy-policy', {
          id: null,
          fromDate: new Date(),
        });
        alertService.showSuccess(`Neue Datenschutzerklärung wurde erstellt. ID: ${response.data.id}`);
        getAllPrivacyPolicies();
      } catch (error) {
        console.error('Fehler beim Erstellen der Datenschutzerklärung:', error);
        alertService.showError('Fehler beim Erstellen der Datenschutzerklärung.');
      }
    };

    const getAllPrivacyPolicies = async () => {
      try {
        const response = await axios.get('api/privacy-policy');
        privacyPolicies.value = response.data;
      } catch (error) {
        console.error('Fehler beim Abrufen der Datenschutzerklärungen:', error);
      }
    };

    getAllPrivacyPolicies();

    return {
      createNewPrivacyPolicy,
      privacyPolicies,
      getAllPrivacyPolicies,
    };
  },
  methods: {
    formatDate(dateString: string): string {
      const options: Intl.DateTimeFormatOptions = {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
      };
      return new Date(dateString).toLocaleDateString('de-DE', options);
    },
  },
});
