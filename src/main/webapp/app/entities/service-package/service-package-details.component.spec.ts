/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ServicePackageDetails from './service-package-details.vue';
import ServicePackageService from './service-package.service';
import AlertService from '@/shared/alert/alert.service';

type ServicePackageDetailsComponentType = InstanceType<typeof ServicePackageDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const servicePackageSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ServicePackage Management Detail Component', () => {
    let servicePackageServiceStub: SinonStubbedInstance<ServicePackageService>;
    let mountOptions: MountingOptions<ServicePackageDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      servicePackageServiceStub = sinon.createStubInstance<ServicePackageService>(ServicePackageService);

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
          servicePackageService: () => servicePackageServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        servicePackageServiceStub.find.resolves(servicePackageSample);
        route = {
          params: {
            servicePackageId: '' + 123,
          },
        };
        const wrapper = shallowMount(ServicePackageDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.servicePackage).toMatchObject(servicePackageSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        servicePackageServiceStub.find.resolves(servicePackageSample);
        const wrapper = shallowMount(ServicePackageDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
