/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import BoothUpdate from './booth-update.vue';
import BoothService from './booth.service';
import AlertService from '@/shared/alert/alert.service';

import LocationService from '@/entities/location/location.service';
import ServicePackageService from '@/entities/service-package/service-package.service';

type BoothUpdateComponentType = InstanceType<typeof BoothUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const boothSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<BoothUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Booth Management Update Component', () => {
    let comp: BoothUpdateComponentType;
    let boothServiceStub: SinonStubbedInstance<BoothService>;

    beforeEach(() => {
      route = {};
      boothServiceStub = sinon.createStubInstance<BoothService>(BoothService);
      boothServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          boothService: () => boothServiceStub,
          locationService: () =>
            sinon.createStubInstance<LocationService>(LocationService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          servicePackageService: () =>
            sinon.createStubInstance<ServicePackageService>(ServicePackageService, {
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
        const wrapper = shallowMount(BoothUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.booth = boothSample;
        boothServiceStub.update.resolves(boothSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(boothServiceStub.update.calledWith(boothSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        boothServiceStub.create.resolves(entity);
        const wrapper = shallowMount(BoothUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.booth = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(boothServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        boothServiceStub.find.resolves(boothSample);
        boothServiceStub.retrieve.resolves([boothSample]);

        // WHEN
        route = {
          params: {
            boothId: '' + boothSample.id,
          },
        };
        const wrapper = shallowMount(BoothUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.booth).toMatchObject(boothSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        boothServiceStub.find.resolves(boothSample);
        const wrapper = shallowMount(BoothUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
