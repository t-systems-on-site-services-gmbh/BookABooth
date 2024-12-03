import { type ComputedRef, defineComponent, onMounted, inject, ref, type Ref } from 'vue';

import type LoginService from '@/account/login.service';
import type AccountService from '@/account/account.service';
import axios from 'axios';

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const loginService = inject<LoginService>('loginService');
    const accountService = inject<AccountService>('accountService');

    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');

    const hasAnyAuthorityValues: Ref<any> = ref({});
    const verified = ref(false); // Beispielwert, anpassen nach Bedarf
    const address = ref(true); // Beispielwert, anpassen nach Bedarf
    const logo = ref(true); // Beispielwert, anpassen nach Bedarf
    const pressContact = ref(true); // Beispielwert, anpassen nach Bedarf
    const companyDescription = ref(true); // Beispielwert, anpassen nach Bedarf
    const bookingStatus = ref(null); // Beispielwert, anpassen nach Bedarf

    const fetchUserChecklist = async () => {
      try {
        const response = await axios.get('api/checklist');
        const checklist = response.data;

        // Setzen Sie die Werte basierend auf der Antwort
        verified.value = checklist.verified;
        address.value = checklist.address;
        logo.value = checklist.logo;
        pressContact.value = checklist.pressContact;
        companyDescription.value = checklist.companyDescription;
        bookingStatus.value = checklist.bookingStatus;
      } catch (error) {
        console.error('Fehler beim Abrufen der Checkliste:', error);
      }
    };

    // Rufen Sie die Methode auf, wenn die Komponente gemountet wird
    onMounted(() => {
      fetchUserChecklist();
    });
    const openLogin = () => {
      loginService.openLogin();
    };

    return {
      authenticated,
      accountService,
      verified,
      bookingStatus,
      address,
      logo,
      pressContact,
      companyDescription,
      openLogin,
      username,
      hasAnyAuthorityValues,
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
