import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import BoothUserService from './booth-user.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import CompanyService from '@/entities/company/company.service';
import { type ICompany } from '@/shared/model/company.model';
import { type IBoothUser, BoothUser } from '@/shared/model/booth-user.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BoothUserUpdate',
  setup() {
    const boothUserService = inject('boothUserService', () => new BoothUserService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const boothUser: Ref<IBoothUser> = ref(new BoothUser());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const companyService = inject('companyService', () => new CompanyService());

    const companies: Ref<ICompany[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'de'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveBoothUser = async boothUserId => {
      try {
        const res = await boothUserService().find(boothUserId);
        res.verified = new Date(res.verified);
        res.lastLogin = new Date(res.lastLogin);
        boothUser.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.boothUserId) {
      retrieveBoothUser(route.params.boothUserId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      companyService()
        .retrieve()
        .then(res => {
          companies.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const validations = useValidation();
    const validationRules = {
      phone: {
        maxLength: validations.maxLength('Dieses Feld darf max. 20 Zeichen lang sein.', 20),
      },
      note: {},
      verificationCode: {},
      verified: {},
      lastLogin: {},
      disabled: {},
      user: {},
      company: {},
    };
    const v$ = useVuelidate(validationRules, boothUser as any);
    v$.value.$validate();

    return {
      boothUserService,
      alertService,
      boothUser,
      previousState,
      isSaving,
      currentLanguage,
      users,
      companies,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: boothUser }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.boothUser.id) {
        this.boothUserService()
          .update(this.boothUser)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A BoothUser is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.boothUserService()
          .create(this.boothUser)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A BoothUser is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
