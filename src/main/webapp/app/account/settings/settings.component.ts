import { computed, type ComputedRef, defineComponent, inject, ref, type Ref } from 'vue';
import { useVuelidate } from '@vuelidate/core';
import { email, maxLength, minLength, required } from '@vuelidate/validators';
import axios from 'axios';
import { EMAIL_ALREADY_USED_TYPE } from '@/constants';
import { useStore } from '@/store';

const validations = {
  settingsAccount: {
    companyName: {
      required,
      minLength: minLength(1),
      maxLength: maxLength(100),
    },
    billingAddress: {
      required,
      minLength: minLength(1),
      maxLength: maxLength(254),
    },
    firstName: {
      required,
      minLength: minLength(1),
      maxLength: maxLength(50),
    },
    lastName: {
      required,
      minLength: minLength(1),
      maxLength: maxLength(50),
    },
    email: {
      required,
      email,
      minLength: minLength(5),
      maxLength: maxLength(254),
    },
    description: {
      maxLength: maxLength(254),
    },
    logoUpload: {},
    exhibitorList: {},
  },
  deleteAccount: {
    passwordConfirm: {
      required,
    },
  },
};

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Settings',
  validations,
  setup() {
    const store = useStore();

    const success: Ref<string> = ref(null);
    const error: Ref<string> = ref(null);
    const errorEmailExists: Ref<string> = ref(null);

    const settingsAccount = computed(() => store.account);
    const waitingList = ref(null);
    const username = inject<ComputedRef<string>>('currentUsername', () => computed(() => store.account?.login), true);
    const exhibitorList = inject<ComputedRef<Boolean>>('exhibitorList', () => computed(() => store.account?.exhibitorList), true);
    const preview = ref(null);
    const image = ref(false);
    const bookingStatus = ref(null);
    const deleteAccount = ref(null);
    const passwordConfirm = ref('');
    const deleteError: Ref<boolean> = ref(false);

    const isChecked = computed(() => {
      return exhibitorList.value ? 'Sie befinden sich in der Ausstellerliste' : 'Sie befinden sich nicht in der Ausstellerliste';
    });

    return {
      v$: useVuelidate(),
      success,
      error,
      errorEmailExists,
      settingsAccount,
      username,
      preview,
      image,
      waitingList,
      bookingStatus,
      exhibitorList,
      isChecked,
      passwordConfirm,
      deleteAccount,
      deleteError,
    };
  },
  methods: {
    save() {
      this.error = null;
      this.errorEmailExists = null;
      return axios
        .post('api/account', this.settingsAccount)
        .then(() => {
          this.error = null;
          this.success = 'OK';
          this.errorEmailExists = null;
        })
        .catch(ex => {
          this.success = null;
          this.error = 'ERROR';
          if (ex.response.status === 400 && ex.response.data.type === EMAIL_ALREADY_USED_TYPE) {
            this.errorEmailExists = 'ERROR';
            this.error = null;
          }
        });
    },
    previewImage: function (event: { target: any }) {
      this.logo = this.$refs.file.files[0];
      this.image = true;
      this.preview = URL.createObjectURL(this.logo);
    },
    showModal() {
      this.$refs['deleteAcc-modal'].show();
    },
    hideModal() {
      this.$refs['deleteAcc-modal'].hide();
    },
    resetModal() {
      this.passwordConfirm = '';
      this.deleteError = false;
    },
    async confirmDelete() {
      try {
        const response = await axios.delete('api/account/delete-account', {
          data: {
            currentPassword: this.passwordConfirm,
          },
          headers: {
            'Content-Type': 'application/json',
          },
        });
        console.log(response);
        if (response.status === 200) {
          this.deleteError = false;
          console.log('Account wurde gelöscht');
          this.hideModal();
        }
      } catch (ex) {
        this.deleteError = true;
        console.error('Fehler beim Löschen des Accounts:', ex);
      }
    },
    async confirmBooking() {
      const response = await axios.put('api/account/confirm-booking', this.settingsAccount);
      console.log(response);
      if (response && response.status === 200) {
        console.log('Buchung wurde erfolgreich bestätigt');
        this.$router.go();
      } else {
        console.log('Es ist ein Fehler aufgetreten');
      }
    },
    async removeWaitingList() {
      const response = await axios.put('api/account/update-waitinglist', this.settingsAccount);
      console.log(response);
      if (response && response.status === 200) {
        console.log('Erfolgreich von der Warteliste entfernt');
        this.$router.go();
      } else {
        console.log('Es ist ein Fehler aufgetreten');
      }
    },
    async setCanceled() {
      const response = await axios.put('api/account/cancel-booking', this.settingsAccount);
      console.log(response);
      if (response && response.status === 200) {
        console.log('Buchung wurde erfolgreich storniert.');
        this.$router.go();
      } else {
        console.log('Es ist ein Fehler aufgetreten');
      }
    },
  },
});
