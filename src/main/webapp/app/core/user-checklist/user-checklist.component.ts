import axios from 'axios';
import { computed, defineComponent, inject, onBeforeUnmount, onMounted, onUpdated, ref, type ComputedRef, type Ref } from 'vue';
import SystemService from '@/entities/system/system.service';
import { type ISystem } from '@/shared/model/system.model';
import UserChecklistService from '@/core/user-checklist/user-checklist.service';
import type { IUserChecklist } from '@/shared/model/user-checklist.model';
import alertService from '@/shared/alert/alert.service';
import { useRouter } from 'vue-router';
import { useStore } from '@/store';
import type AccountService from '@/account/account.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const store = useStore();
    const systemService = inject('systemService', () => new SystemService());
    const accountService = inject<AccountService>('accountService');
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const allBoothsOccupied = ref(false);
    const isOnWaitingList = ref(false);
    const alreadyFetched = ref(false);
    const router = useRouter();
    const account = computed(() => store.account);

    const system: Ref<ISystem> = ref();
    const userChecklistService = inject('userChecklistService', () => new UserChecklistService());

    const checklist: Ref<IUserChecklist> = ref();

    const initRelationships = () => {
      userChecklistService()
        .checklist()
        .then((res: { data: IUserChecklist }) => {
          checklist.value = res;
        });
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

    const retrieveSystem = async () => {
      try {
        const res = await systemService().retrieve();
        system.value = res?.data;
      } catch (error) {
        console.error('Fehler beim Abrufen des Systems:', error);
      }
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

    onMounted(() => {
      if (authenticated.value && !alreadyFetched.value) {
        checkBooths();
        initRelationships();
        retrieveSystem();
        alreadyFetched.value = true;
      }
      // Außerdem Methoden für die Einblendung beim Löschen des Accounts
      const accountDeleted = sessionStorage.getItem('accountDeleted');
      if (accountDeleted === 'true') {
        showAccountDeletedToast();
      }
    });

    onBeforeUnmount(() => {
      sessionStorage.removeItem('accountDeleted');
    });

    return {
      accountService,
      account,
      authenticated,
      system,
      checklist,
      addToWaitingList,
      isOnWaitingList,
      allBoothsOccupied,
    };
  },
});
