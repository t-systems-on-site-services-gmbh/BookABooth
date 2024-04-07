/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import System from './system.vue';
import SystemService from './system.service';
import AlertService from '@/shared/alert/alert.service';

type SystemComponentType = InstanceType<typeof System>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('System Management Component', () => {
    let systemServiceStub: SinonStubbedInstance<SystemService>;
    let mountOptions: MountingOptions<SystemComponentType>['global'];

    beforeEach(() => {
      systemServiceStub = sinon.createStubInstance<SystemService>(SystemService);
      systemServiceStub.retrieve.resolves({ headers: {} });

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
          systemService: () => systemServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        systemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(System, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(systemServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.systems[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: SystemComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(System, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        systemServiceStub.retrieve.reset();
        systemServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        systemServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeSystem();
        await comp.$nextTick(); // clear components

        // THEN
        expect(systemServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(systemServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
