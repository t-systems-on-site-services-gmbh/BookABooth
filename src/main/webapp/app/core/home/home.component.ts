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

    const openLogin = () => {
      loginService.openLogin();
    };

    return {
      authenticated,
      accountService,
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
