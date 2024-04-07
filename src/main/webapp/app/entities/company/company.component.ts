import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import CompanyService from './company.service';
import { type ICompany } from '@/shared/model/company.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Company',
  setup() {
    const companyService = inject('companyService', () => new CompanyService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const companies: Ref<ICompany[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveCompanys = async () => {
      isFetching.value = true;
      try {
        const res = await companyService().retrieve();
        companies.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveCompanys();
    };

    onMounted(async () => {
      await retrieveCompanys();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ICompany) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeCompany = async () => {
      try {
        await companyService().delete(removeId.value);
        const message = 'A Company is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveCompanys();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      companies,
      handleSyncList,
      isFetching,
      retrieveCompanys,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeCompany,
    };
  },
});
