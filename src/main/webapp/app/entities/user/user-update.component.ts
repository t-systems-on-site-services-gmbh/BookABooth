import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import UserService from './user.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';
import { type IUser, User } from '@/shared/model/user.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserUpdate',
  setup() {
    const userService = inject('userService', () => new UserService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const user: Ref<IUser> = ref(new User());

    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'de'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveUser = async userLogin => {
      try {
        const res = await userService().find(userLogin);
        user.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.userLogin) {
      retrieveUser(route.params.userLogin);
    }

    const retrieveCompanies = async () => {
      try {
        const res = await companyService().retrieve();
        companies.value = res.data;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    const initRelationships = () => {
      retrieveCompanies();
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      login: {
        required: validations.required('Dieses Feld wird benötigt.'),
      },
      firstName: {},
      lastName: {},
      email: {},
      company: {},
      authorities: {
        required: validations.required('Dieses Feld wird benötigt.'),
      },
      activated: {},
      langKey: {},
      createdBy: {},
      createdDate: {},
      lastModifiedBy: {},
      lastModifiedDate: {},
    };
    const v$ = useVuelidate(validationRules, user as any);
    v$.value.$validate();

    const selectedAuthority = computed({
      get: () => user.value.authorities?.includes('ROLE_ADMIN') ? 'ROLE_ADMIN' : 'ROLE_USER',
      set: (val: string) => {
        user.value.authorities = val === 'ROLE_ADMIN' ? ['ROLE_USER', 'ROLE_ADMIN'] : ['ROLE_USER'];
      },
    });

    return {
      userService,
      alertService,
      user,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      companies,
      selectedAuthority,
    };
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.user.id) {
        this.userService()
          .update(this.user)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A User is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.userService()
          .create(this.user)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A User is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    getSelected(selectedVals, option, pkField = 'id'): any {
      if (selectedVals) {
        return selectedVals.find(value => option[pkField] === value[pkField]) ?? option;
      }
      return option;
    },
  },
});