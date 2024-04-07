/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ServicePackageUpdate from './service-package-update.vue';
import ServicePackageService from './service-package.service';
import AlertService from '@/shared/alert/alert.service';

import BoothService from '@/entities/booth/booth.service';

type ServicePackageUpdateComponentType = InstanceType<typeof ServicePackageUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const servicePackageSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ServicePackageUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ServicePackage Management Update Component', () => {
    let comp: ServicePackageUpdateComponentType;
    let servicePackageServiceStub: SinonStubbedInstance<ServicePackageService>;

    beforeEach(() => {
      route = {};
      servicePackageServiceStub = sinon.createStubInstance<ServicePackageService>(ServicePackageService);
      servicePackageServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          servicePackageService: () => servicePackageServiceStub,
          boothService: () =>
            sinon.createStubInstance<BoothService>(BoothService, {
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
        const wrapper = shallowMount(ServicePackageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.servicePackage = servicePackageSample;
        servicePackageServiceStub.update.resolves(servicePackageSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(servicePackageServiceStub.update.calledWith(servicePackageSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        servicePackageServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ServicePackageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.servicePackage = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(servicePackageServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        servicePackageServiceStub.find.resolves(servicePackageSample);
        servicePackageServiceStub.retrieve.resolves([servicePackageSample]);

        // WHEN
        route = {
          params: {
            servicePackageId: '' + servicePackageSample.id,
          },
        };
        const wrapper = shallowMount(ServicePackageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.servicePackage).toMatchObject(servicePackageSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        servicePackageServiceStub.find.resolves(servicePackageSample);
        const wrapper = shallowMount(ServicePackageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
