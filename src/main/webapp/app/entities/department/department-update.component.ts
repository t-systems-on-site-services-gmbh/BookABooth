import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import DepartmentService from './department.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CompanyService from '@/entities/company/company.service';
import { type ICompany } from '@/shared/model/company.model';
import { type IDepartment, Department } from '@/shared/model/department.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'DepartmentUpdate',
  setup() {
    const departmentService = inject('departmentService', () => new DepartmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const department: Ref<IDepartment> = ref(new Department());

    const companyService = inject('companyService', () => new CompanyService());

    const companies: Ref<ICompany[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'de'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveDepartment = async departmentId => {
      try {
        const res = await departmentService().find(departmentId);
        department.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.departmentId) {
      retrieveDepartment(route.params.departmentId);
    }

    const initRelationships = () => {
      companyService()
        .retrieve()
        .then(res => {
          companies.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      name: {},
      description: {},
      companies: {},
    };
    const v$ = useVuelidate(validationRules, department as any);
    v$.value.$validate();

    return {
      departmentService,
      alertService,
      department,
      previousState,
      isSaving,
      currentLanguage,
      companies,
      v$,
    };
  },
  created(): void {
    this.department.companies = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.department.id) {
        this.departmentService()
          .update(this.department)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Department is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.departmentService()
          .create(this.department)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Department is created with identifier ' + param.id);
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
