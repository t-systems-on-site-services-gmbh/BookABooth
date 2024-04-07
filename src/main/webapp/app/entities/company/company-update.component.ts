import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CompanyService from './company.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import DepartmentService from '@/entities/department/department.service';
import { type IDepartment } from '@/shared/model/department.model';
import ContactService from '@/entities/contact/contact.service';
import { type IContact } from '@/shared/model/contact.model';
import { type ICompany, Company } from '@/shared/model/company.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CompanyUpdate',
  setup() {
    const companyService = inject('companyService', () => new CompanyService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const company: Ref<ICompany> = ref(new Company());

    const departmentService = inject('departmentService', () => new DepartmentService());

    const departments: Ref<IDepartment[]> = ref([]);

    const contactService = inject('contactService', () => new ContactService());

    const contacts: Ref<IContact[]> = ref([]);
    const isSaving = ref(false);
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

    const initRelationships = () => {
      departmentService()
        .retrieve()
        .then(res => {
          departments.value = res.data;
        });
      contactService()
        .retrieve()
        .then(res => {
          contacts.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      name: {
        maxLength: validations.maxLength('Dieses Feld darf max. 200 Zeichen lang sein.', 200),
      },
      mail: {},
      billingAddress: {},
      logo: {},
      description: {},
      waitingList: {},
      exhibitorList: {},
      departments: {},
      contacts: {},
    };
    const v$ = useVuelidate(validationRules, company as any);
    v$.value.$validate();

    return {
      companyService,
      alertService,
      company,
      previousState,
      isSaving,
      currentLanguage,
      departments,
      contacts,
      v$,
    };
  },
  created(): void {
    this.company.departments = [];
    this.company.contacts = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.company.id) {
        this.companyService()
          .update(this.company)
          .then(param => {
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

    getSelected(selectedVals, option, pkField = 'id'): any {
      if (selectedVals) {
        return selectedVals.find(value => option[pkField] === value[pkField]) ?? option;
      }
      return option;
    },
  },
});
