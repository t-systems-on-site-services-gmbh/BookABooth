import { type ComputedRef, defineComponent, inject, onBeforeUnmount, onMounted } from 'vue';
import { useAlertService } from '@/shared/alert/alert.service';
import type LoginService from '@/account/login.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const loginService = inject<LoginService>('loginService');

    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');

    const alertService = inject('alertService', () => useAlertService(), true);

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

    onMounted(() => {
      const accountDeleted = sessionStorage.getItem('accountDeleted');
      if (accountDeleted === 'true') {
        showAccountDeletedToast();
      }
    });

    onBeforeUnmount(() => {
      sessionStorage.removeItem('accountDeleted');
    });

    return {
      authenticated,
      username,
      openLogin,
    };
  },
});
