/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Contact from './contact.vue';
import ContactService from './contact.service';
import AlertService from '@/shared/alert/alert.service';

type ContactComponentType = InstanceType<typeof Contact>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Contact Management Component', () => {
    let contactServiceStub: SinonStubbedInstance<ContactService>;
    let mountOptions: MountingOptions<ContactComponentType>['global'];

    beforeEach(() => {
      contactServiceStub = sinon.createStubInstance<ContactService>(ContactService);
      contactServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          contactService: () => contactServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        contactServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Contact, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(contactServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.contacts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: ContactComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Contact, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        contactServiceStub.retrieve.reset();
        contactServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        contactServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeContact();
        await comp.$nextTick(); // clear components

        // THEN
        expect(contactServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(contactServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
