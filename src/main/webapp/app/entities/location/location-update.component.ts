import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import LocationService from './location.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ILocation, Location } from '@/shared/model/location.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'LocationUpdate',
  setup() {
    const locationService = inject('locationService', () => new LocationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const location: Ref<ILocation> = ref(new Location());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'de'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveLocation = async locationId => {
      try {
        const res = await locationService().find(locationId);
        location.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.locationId) {
      retrieveLocation(route.params.locationId);
    }

    const validations = useValidation();
    const validationRules = {
      location: {
        maxLength: validations.maxLength('Dieses Feld darf max. 200 Zeichen lang sein.', 200),
      },
      imageUrl: {
        maxLength: validations.maxLength('Dieses Feld darf max. 200 Zeichen lang sein.', 200),
      },
    };
    const v$ = useVuelidate(validationRules, location as any);
    v$.value.$validate();

    return {
      locationService,
      alertService,
      location,
      previousState,
      isSaving,
      currentLanguage,
      v$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.location.id) {
        this.locationService()
          .update(this.location)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo('A Location is updated with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.locationService()
          .create(this.location)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess('A Location is created with identifier ' + param.id);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
    handleImageUpload(event: Event): void {
      const input = event.target as HTMLInputElement;
      if (input.files && input.files[0]) {
        const file = input.files[0];
        console.log('Hochgeladene Datei:', file);

        const reader = new FileReader();
        reader.onload = (e: ProgressEvent<FileReader>) => {
          const fileContent = e.target?.result;

          // convert to base64 string;
          const contentBase64 = btoa(fileContent as string);

          console.log('Dateiinhalt:', contentBase64);

          // send file to server
          this.isSaving = true;
          this.locationService()
            .uploadImage(this.location, contentBase64)
            .then(param => {
              this.isSaving = false;
              this.previousState();
              this.alertService.showInfo('Updated image of Location ' + param.id);
            })
            .catch(error => {
              this.isSaving = false;
              this.alertService.showHttpError(error.response);
            });
        };
        reader.readAsBinaryString(file);
      }
    },
  },
});
