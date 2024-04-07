/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ContactUpdate from './contact-update.vue';
import ContactService from './contact.service';
import AlertService from '@/shared/alert/alert.service';

import CompanyService from '@/entities/company/company.service';

type ContactUpdateComponentType = InstanceType<typeof ContactUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const contactSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ContactUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Contact Management Update Component', () => {
    let comp: ContactUpdateComponentType;
    let contactServiceStub: SinonStubbedInstance<ContactService>;

    beforeEach(() => {
      route = {};
      contactServiceStub = sinon.createStubInstance<ContactService>(ContactService);
      contactServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          contactService: () => contactServiceStub,
          companyService: () =>
            sinon.createStubInstance<CompanyService>(CompanyService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ContactUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.contact = contactSample;
        contactServiceStub.update.resolves(contactSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(contactServiceStub.update.calledWith(contactSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        contactServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ContactUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.contact = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(contactServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        contactServiceStub.find.resolves(contactSample);
        contactServiceStub.retrieve.resolves([contactSample]);

        // WHEN
        route = {
          params: {
            contactId: '' + contactSample.id,
          },
        };
        const wrapper = shallowMount(ContactUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.contact).toMatchObject(contactSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        contactServiceStub.find.resolves(contactSample);
        const wrapper = shallowMount(ContactUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
