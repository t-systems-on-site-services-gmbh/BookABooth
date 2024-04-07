import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import CompanyService from './company.service';
import { type ICompany } from '@/shared/model/company.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CompanyDetails',
  setup() {
    const companyService = inject('companyService', () => new CompanyService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const company: Ref<ICompany> = ref({});

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

    return {
      alertService,
      company,

      previousState,
    };
  },
});
