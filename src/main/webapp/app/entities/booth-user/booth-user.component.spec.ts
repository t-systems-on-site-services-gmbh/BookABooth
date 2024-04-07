/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import BoothUser from './booth-user.vue';
import BoothUserService from './booth-user.service';
import AlertService from '@/shared/alert/alert.service';

type BoothUserComponentType = InstanceType<typeof BoothUser>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('BoothUser Management Component', () => {
    let boothUserServiceStub: SinonStubbedInstance<BoothUserService>;
    let mountOptions: MountingOptions<BoothUserComponentType>['global'];

    beforeEach(() => {
      boothUserServiceStub = sinon.createStubInstance<BoothUserService>(BoothUserService);
      boothUserServiceStub.retrieve.resolves({ headers: {} });

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
          boothUserService: () => boothUserServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        boothUserServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(BoothUser, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(boothUserServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.boothUsers[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: BoothUserComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(BoothUser, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        boothUserServiceStub.retrieve.reset();
        boothUserServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        boothUserServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeBoothUser();
        await comp.$nextTick(); // clear components

        // THEN
        expect(boothUserServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(boothUserServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
