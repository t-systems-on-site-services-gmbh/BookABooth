import { computed, type ComputedRef, defineComponent, inject, onBeforeUnmount, onUpdated, ref, type Ref } from 'vue';
import type LoginService from '@/account/login.service';
import type AccountService from '@/account/account.service';
import { useStore } from '@/store';
import AdminDashboard from '@/admin/dashboard/admin-dashboard.vue';
import UserChecklist from '@/core/user-checklist/user-checklist.vue';

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

    const openLogin = () => {
      loginService.openLogin();
    };

    return {
      authenticated,
      accountService,
      account,
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
