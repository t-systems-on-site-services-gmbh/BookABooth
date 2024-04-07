/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import SystemUpdate from './system-update.vue';
import SystemService from './system.service';
import AlertService from '@/shared/alert/alert.service';

type SystemUpdateComponentType = InstanceType<typeof SystemUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const systemSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<SystemUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('System Management Update Component', () => {
    let comp: SystemUpdateComponentType;
    let systemServiceStub: SinonStubbedInstance<SystemService>;

    beforeEach(() => {
      route = {};
      systemServiceStub = sinon.createStubInstance<SystemService>(SystemService);
      systemServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          systemService: () => systemServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(SystemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.system = systemSample;
        systemServiceStub.update.resolves(systemSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(systemServiceStub.update.calledWith(systemSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        systemServiceStub.create.resolves(entity);
        const wrapper = shallowMount(SystemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.system = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(systemServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        systemServiceStub.find.resolves(systemSample);
        systemServiceStub.retrieve.resolves([systemSample]);

        // WHEN
        route = {
          params: {
            systemId: '' + systemSample.id,
          },
        };
        const wrapper = shallowMount(SystemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.system).toMatchObject(systemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        systemServiceStub.find.resolves(systemSample);
        const wrapper = shallowMount(SystemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
