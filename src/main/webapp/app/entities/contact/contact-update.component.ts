import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ContactService from './contact.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CompanyService from '@/entities/company/company.service';
import { type ICompany } from '@/shared/model/company.model';
import { type IContact, Contact } from '@/shared/model/contact.model';
import { ContactResponsibility } from '@/shared/model/enumerations/contact-responsibility.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ContactUpdate',
  setup() {
    const contactService = inject('contactService', () => new ContactService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const contact: Ref<IContact> = ref(new Contact());

    const companyService = inject('companyService', () => new CompanyService());

    const companies: Ref<ICompany[]> = ref([]);
    const contactResponsibilityValues: Ref<string[]> = ref(Object.keys(ContactResponsibility));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'de'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
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
      firstName: {
        maxLength: validations.maxLength('Dieses Feld darf max. 200 Zeichen lang sein.', 200),
      },
      lastName: {
        maxLength: validations.maxLength('Dieses Feld darf max. 200 Zeichen lang sein.', 200),
      },
      mail: {},
      phone: {
        maxLength: validations.maxLength('Dieses Feld darf max. 20 Zeichen lang sein.', 20),
      },
      responsibility: {},
      note: {},
      companies: {},
    };
    const v$ = useVuelidate(validationRules, contact as any);
    v$.value.$validate();

    return {
      contactService,
      alertService,
      contact,
      previousState,
      contactResponsibilityValues,
      isSaving,
      currentLanguage,
      companies,
      ...dataUtils,
      v$,
    };
  },
  created(): void {
    this.contact.companies = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.contact.id) {
        this.contactService()
          .update(this.contact)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Contact is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.contactService()
          .create(this.contact)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Contact is created with identifier ' + param.id);
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
