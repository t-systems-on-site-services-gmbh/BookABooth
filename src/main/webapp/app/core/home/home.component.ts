import { computed, type ComputedRef, defineComponent, inject, onBeforeUnmount, onMounted, onUpdated, ref, type Ref } from 'vue';
import type LoginService from '@/account/login.service';
import type AccountService from '@/account/account.service';
import { useStore } from '@/store';
import AdminDashboard from '@/admin/dashboard/admin-dashboard.vue';
import UserChecklist from '@/core/user-checklist/user-checklist.vue';
import { type IPrivacyPolicy } from '@/shared/model/privacy-policy.model';
import { useAlertService } from '@/shared/alert/alert.service';
import axios from 'axios';

export default defineComponent({
  compatConfig: { MODE: 3 },
  components: {
    adminDashboard: AdminDashboard,
    userChecklist: UserChecklist,
  },
  setup() {
    const store = useStore();
    const loginService = inject<LoginService>('loginService');
    const accountService = inject<AccountService>('accountService');
    const account = computed(() => store.account);
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');
    const hasAnyAuthorityValues: Ref<any> = ref({});
    const latestPrivacyPolicy: Ref<IPrivacyPolicy> = ref(null);
    const acceptedPrivacyPolicy: Ref<IPrivacyPolicy> = ref(null);
    const privacyPolicyModal = ref(null);
    const alertService = inject('alertService', () => useAlertService(), true);

    const openLogin = () => {
      loginService.openLogin();
    };

    const showPrivacyPolicyModal = () => {
      privacyPolicyModal.value.show();
    };

    const closePrivacyPolicyModal = async () => {
      try {
        // Sende die POST-Anfrage an den Server
        await axios.post('api/privacy-policy/accept/');

        // Aktualisiere die akzeptierte Datenschutzerklärung im Frontend
        acceptedPrivacyPolicy.value = latestPrivacyPolicy.value;

        // Schließe das Modal
        privacyPolicyModal.value.hide();

        alertService.showSuccess('Die Datenschutzerklärung wurde erfolgreich akzeptiert.');
      } catch (error: any) {
        console.error('Fehler beim Speichern der Datenschutzerklärung:', error);
      }
    };

    const checkPrivacyPolicy = (): boolean => {
      if (latestPrivacyPolicy.value.id === acceptedPrivacyPolicy.value.id) {
        return true;
      } else {
        return false;
      }
    };

    const fetchPrivacyPolicyData = async () => {
      try {
        const response = await axios.get('api/privacy-policy/latest');
        latestPrivacyPolicy.value = response.data;

        const acceptedPrivacyPolicyResponse = await axios.get('api/privacy-policy/check');
        acceptedPrivacyPolicy.value = acceptedPrivacyPolicyResponse.data;

        if (!checkPrivacyPolicy()) {
          showPrivacyPolicyModal();
        }
      } catch (error) {
        console.error('Fehler beim Abrufen der Datenschutzerklärung:', error);
      }
    };

    onUpdated(() => {
      if (authenticated.value) {
        fetchPrivacyPolicyData();
      }
    });

    return {
      privacyPolicyModal,
      showPrivacyPolicyModal,
      closePrivacyPolicyModal,
      authenticated,
      accountService,
      account,
      openLogin,
      username,
      hasAnyAuthorityValues,
      latestPrivacyPolicy,
      acceptedPrivacyPolicy,
    };
  },
  methods: {
    hasAnyAuthority(authorities: any): boolean {
      this.accountService.hasAnyAuthorityAndCheckAuth(authorities).then(value => {
        if (this.hasAnyAuthorityValues[authorities] !== value) {
          this.hasAnyAuthorityValues = { ...this.hasAnyAuthorityValues, [authorities]: value };
        }
      });
      return this.hasAnyAuthorityValues[authorities] ?? false;
    },
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
