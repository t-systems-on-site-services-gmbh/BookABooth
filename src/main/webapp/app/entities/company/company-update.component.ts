import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CompanyService from './company.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';
import { type ICompany, Company } from '@/shared/model/company.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CompanyUpdate',
  setup() {
    const companyService = inject('companyService', () => new CompanyService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const company: Ref<ICompany> = ref(new Company());

    const isSaving = ref(false);
    const selectedLogoFile: Ref<File | null> = ref(null);
    const logoPreview: Ref<string | null> = ref(null);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'de'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCompany = async companyId => {
      try {
        const res = await companyService().find(companyId);
        company.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.companyId) {
      retrieveCompany(route.params.companyId);
    }

    const initRelationships = () => {};

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      name: {
        maxLength: validations.maxLength('Dieses Feld darf max. 200 Zeichen lang sein.', 200),
      },
      mail: {},
      billingAddressRow1: {},
      billingAddressRow2: {},
      billingAddressRow3: {},
      billingAddressRow4: {},
      billingZipCode: {},
      billingCity: {},
      logo: {},
      description: {},
      waitingList: {},
      exhibitorList: {},
    };
    const v$ = useVuelidate(validationRules, company as any);
    v$.value.$validate();

    return {
      companyService,
      alertService,
      company,
      previousState,
      isSaving,
      selectedLogoFile,
      logoPreview,
      currentLanguage,
      v$,
    };
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.company.id) {
        this.companyService()
          .update(this.company)
          .then(param => {
            this.uploadLogo(param.id);
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Company is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.companyService()
          .create(this.company)
          .then(param => {
            this.uploadLogo(param.id);
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Company is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    onLogoChange(event: Event): void {
      const file = (event.target as HTMLInputElement).files?.[0];
      if (file) {
        this.selectedLogoFile = file;
        this.logoPreview = URL.createObjectURL(file);
      }
    },

    uploadLogo(companyId: number): void {
      if (!this.selectedLogoFile) return;
      const reader = new FileReader();
      reader.onload = (e: ProgressEvent<FileReader>) => {
        const contentBase64 = btoa(e.target?.result as string);
        this.companyService()
          .uploadImage(companyId, contentBase64)
          .then(param => {
            this.company.logo = param.logo;
          })
          .catch(error => {
            this.alertService.showHttpError(error.response);
          });
      };
      reader.readAsBinaryString(this.selectedLogoFile);
    },

    getSelected(selectedVals, option, pkField = 'id'): any {
      if (selectedVals) {
        return selectedVals.find(value => option[pkField] === value[pkField]) ?? option;
      }
      return option;
    },
  },
});
