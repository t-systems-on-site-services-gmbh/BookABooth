import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';

import ContactService from './contact.service';
import { type IContact } from '@/shared/model/contact.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Contact',
  setup() {
    const dataUtils = useDataUtils();
    const contactService = inject('contactService', () => new ContactService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const contacts: Ref<IContact[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveContacts = async () => {
      isFetching.value = true;
      try {
        const res = await contactService().retrieve();
        contacts.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveContacts();
    };

    onMounted(async () => {
      await retrieveContacts();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IContact) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeContact = async () => {
      try {
        await contactService().delete(removeId.value);
        const message = 'A Contact is deleted with identifier ' + removeId.value;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveContacts();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      contacts,
      handleSyncList,
      isFetching,
      retrieveContacts,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeContact,
      ...dataUtils,
    };
  },
});
