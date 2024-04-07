/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CompanyDetails from './company-details.vue';
import CompanyService from './company.service';
import AlertService from '@/shared/alert/alert.service';

type CompanyDetailsComponentType = InstanceType<typeof CompanyDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const companySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Company Management Detail Component', () => {
    let companyServiceStub: SinonStubbedInstance<CompanyService>;
    let mountOptions: MountingOptions<CompanyDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      companyServiceStub = sinon.createStubInstance<CompanyService>(CompanyService);

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
          companyService: () => companyServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        companyServiceStub.find.resolves(companySample);
        route = {
          params: {
            companyId: '' + 123,
          },
        };
        const wrapper = shallowMount(CompanyDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.company).toMatchObject(companySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        companyServiceStub.find.resolves(companySample);
        const wrapper = shallowMount(CompanyDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
