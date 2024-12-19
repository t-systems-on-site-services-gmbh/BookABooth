import { computed, type ComputedRef, defineComponent, inject, onBeforeUnmount, onUpdated, ref, type Ref } from 'vue';
import { useAlertService } from '@/shared/alert/alert.service';
import type LoginService from '@/account/login.service';
import type AccountService from '@/account/account.service';
import axios from 'axios';
import { useStore } from '@/store';
import { useRouter } from 'vue-router';

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const store = useStore();
    const loginService = inject<LoginService>('loginService');
    const accountService = inject<AccountService>('accountService');
    const account = computed(() => store.account);
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');
    const alertService = inject('alertService', () => useAlertService(), true);
    const hasAnyAuthorityValues: Ref<any> = ref({});
    const verified = ref(false); // Beispielwert, anpassen nach Bedarf
    const address = ref(true); // Beispielwert, anpassen nach Bedarf
    const logo = ref(true); // Beispielwert, anpassen nach Bedarf
    const phoneNumber = ref(true); // Beispielwert, anpassen nach Bedarf
    const companyDescription = ref(true); // Beispielwert, anpassen nach Bedarf
    const bookingStatus = ref(null); // Beispielwert, anpassen nach Bedarf
    const allBoothsOccupied = ref(false);
    const isOnWaitingList = ref(false);
    const router = useRouter();

    const fetchUserChecklist = async () => {
      try {
        const response = await axios.get('api/checklist');
        const checklist = response.data;

        // Setzen Sie die Werte basierend auf der Antwort
        verified.value = checklist.verified;
        address.value = checklist.address;
        logo.value = checklist.logo;
        phoneNumber.value = checklist.phoneNumber;
        companyDescription.value = checklist.companyDescription;
        bookingStatus.value = checklist.bookingStatus;
      } catch (error) {
        console.error('Fehler beim Abrufen der Checkliste:', error);
      }
    };

    const addToWaitingList = async () => {
      try {
        const response = await axios.put('api/waitinglist/add-waitinglist', account.value);
        isOnWaitingList.value = true;
        console.log(response);
        router.go(0);
      } catch (error) {
        if (error.response) {
          console.error('Serverfehler:', error.response.data);
        } else if (error.request) {
          console.error('Netzwerkfehler:', error.request);
        } else {
          console.error('Fehler:', error.message);
        }
      }
    };

    // Rufen Sie die Methode auf, wenn die Komponente geupdated wird
    onUpdated(() => {
      fetchUserChecklist();
      // Außerdem Methoden für die Einblendung beim Löschen des Accounts
      const accountDeleted = sessionStorage.getItem('accountDeleted');
      if (accountDeleted === 'true') {
        showAccountDeletedToast();
      }
      checkBooths();
    });

    onBeforeUnmount(() => {
      sessionStorage.removeItem('accountDeleted');
    });

    const openLogin = () => {
      loginService.openLogin();
    };

    const showAccountDeletedToast = () => {
      alertService.bvToast.toast('Account wurde erfolgreich gelöscht', {
        toaster: 'b-toaster-top-center',
        variant: 'success',
        solid: true,
        autoHideDelay: 5000,
      });
    };

    const checkBooths = async () => {
      try {
        const response = await axios.get('api/booths/occupied');
        allBoothsOccupied.value = response.data;
      } catch (error) {
        console.error('Fehler beim Abrufen der Stände:', error);
      }
    };

    return {
      authenticated,
      accountService,
      account,
      verified,
      bookingStatus,
      address,
      logo,
      phoneNumber,
      companyDescription,
      openLogin,
      username,
      hasAnyAuthorityValues,
      allBoothsOccupied,
      isOnWaitingList,
      addToWaitingList,
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
  },
});
