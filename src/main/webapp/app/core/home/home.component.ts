import { type ComputedRef, defineComponent, inject, ref, type Ref } from 'vue';

import type LoginService from '@/account/login.service';
import type AccountService from '@/account/account.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const loginService = inject<LoginService>('loginService');
    const accountService = inject<AccountService>('accountService');

    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');

    const hasAnyAuthorityValues: Ref<any> = ref({});
    const verifiedEmail = ref(false); // Beispielwert, anpassen nach Bedarf
    const address = ref(true); // Beispielwert, anpassen nach Bedarf
    const logo = ref(true); // Beispielwert, anpassen nach Bedarf
    const pressContact = ref(true); // Beispielwert, anpassen nach Bedarf
    const companyDescription = ref(true); // Beispielwert, anpassen nach Bedarf
    const concludedBooking = ref(false); // Beispielwert, anpassen nach Bedarf

    const openLogin = () => {
      loginService.openLogin();
    };

    return {
      authenticated,
      accountService,
      verifiedEmail,
      concludedBooking,
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
