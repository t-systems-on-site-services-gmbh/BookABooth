/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import BoothUserDetails from './booth-user-details.vue';
import BoothUserService from './booth-user.service';
import AlertService from '@/shared/alert/alert.service';

type BoothUserDetailsComponentType = InstanceType<typeof BoothUserDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const boothUserSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('BoothUser Management Detail Component', () => {
    let boothUserServiceStub: SinonStubbedInstance<BoothUserService>;
    let mountOptions: MountingOptions<BoothUserDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      boothUserServiceStub = sinon.createStubInstance<BoothUserService>(BoothUserService);

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
          boothUserService: () => boothUserServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        boothUserServiceStub.find.resolves(boothUserSample);
        route = {
          params: {
            boothUserId: '' + 123,
          },
        };
        const wrapper = shallowMount(BoothUserDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.boothUser).toMatchObject(boothUserSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        boothUserServiceStub.find.resolves(boothUserSample);
        const wrapper = shallowMount(BoothUserDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
