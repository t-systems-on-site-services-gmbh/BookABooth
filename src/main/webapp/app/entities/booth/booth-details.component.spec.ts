/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import BoothDetails from './booth-details.vue';
import BoothService from './booth.service';
import AlertService from '@/shared/alert/alert.service';

type BoothDetailsComponentType = InstanceType<typeof BoothDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const boothSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Booth Management Detail Component', () => {
    let boothServiceStub: SinonStubbedInstance<BoothService>;
    let mountOptions: MountingOptions<BoothDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      boothServiceStub = sinon.createStubInstance<BoothService>(BoothService);

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
          boothService: () => boothServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        boothServiceStub.find.resolves(boothSample);
        route = {
          params: {
            boothId: '' + 123,
          },
        };
        const wrapper = shallowMount(BoothDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.booth).toMatchObject(boothSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        boothServiceStub.find.resolves(boothSample);
        const wrapper = shallowMount(BoothDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
