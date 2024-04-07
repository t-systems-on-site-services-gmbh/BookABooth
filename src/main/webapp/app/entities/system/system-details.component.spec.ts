/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import SystemDetails from './system-details.vue';
import SystemService from './system.service';
import AlertService from '@/shared/alert/alert.service';

type SystemDetailsComponentType = InstanceType<typeof SystemDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const systemSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('System Management Detail Component', () => {
    let systemServiceStub: SinonStubbedInstance<SystemService>;
    let mountOptions: MountingOptions<SystemDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      systemServiceStub = sinon.createStubInstance<SystemService>(SystemService);

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
          systemService: () => systemServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        systemServiceStub.find.resolves(systemSample);
        route = {
          params: {
            systemId: '' + 123,
          },
        };
        const wrapper = shallowMount(SystemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.system).toMatchObject(systemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        systemServiceStub.find.resolves(systemSample);
        const wrapper = shallowMount(SystemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
