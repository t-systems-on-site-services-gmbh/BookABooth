import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ContactService from './contact.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IContact } from '@/shared/model/contact.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ContactDetails',
  setup() {
    const contactService = inject('contactService', () => new ContactService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const contact: Ref<IContact> = ref({});

    const retrieveContact = async contactId => {
      try {
        const res = await contactService().find(contactId);
        contact.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.contactId) {
      retrieveContact(route.params.contactId);
    }

    return {
      alertService,
      contact,

      ...dataUtils,

      previousState,
    };
  },
});
