/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ContactDetails from './contact-details.vue';
import ContactService from './contact.service';
import AlertService from '@/shared/alert/alert.service';

type ContactDetailsComponentType = InstanceType<typeof ContactDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const contactSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Contact Management Detail Component', () => {
    let contactServiceStub: SinonStubbedInstance<ContactService>;
    let mountOptions: MountingOptions<ContactDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      contactServiceStub = sinon.createStubInstance<ContactService>(ContactService);

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          contactService: () => contactServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        contactServiceStub.find.resolves(contactSample);
        route = {
          params: {
            contactId: '' + 123,
          },
        };
        const wrapper = shallowMount(ContactDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.contact).toMatchObject(contactSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        contactServiceStub.find.resolves(contactSample);
        const wrapper = shallowMount(ContactDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
