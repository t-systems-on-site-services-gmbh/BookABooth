import { computed, type ComputedRef, defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useVuelidate } from '@vuelidate/core';
import { email, maxLength, minLength, required, requiredUnless } from '@vuelidate/validators';
import axios from 'axios';
import { EMAIL_ALREADY_USED_TYPE } from '@/constants';
import { useStore } from '@/store';
import { useAlertService } from '@/shared/alert/alert.service';
import { type ICompany, Company } from '@/shared/model/company.model';
import CompanyService from '@/entities/company/company.service';
import type AccountService from 'account/account.service';
import SystemService from '@/entities/system/system.service';
import { type ISystem } from '@/shared/model/system.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Settings',
  setup() {
    const store = useStore();

    const siteUrl = inject('siteUrl', () => computed(() => window.location.origin), true);
    const alertService = inject('alertService', () => useAlertService(), true);
    const company: Ref<ICompany> = ref(new Company());
    const companyService = inject('companyService', () => new CompanyService());
    const accountService = inject<AccountService>('accountService');
    const systemService = inject('systemService', () => new SystemService());
    const system: Ref<ISystem> = ref();
    const success: Ref<string> = ref(null);
    const error: Ref<string> = ref(null);
    const errorEmailExists: Ref<string> = ref(null);

    const settingsAccount = computed(() => store.account);
    const waitingList = ref(null);
    const username = inject<ComputedRef<string>>('currentUsername', () => computed(() => store.account?.login), true);
    const exhibitorList = inject<ComputedRef<Boolean>>('exhibitorList', () => computed(() => store.account.company?.exhibitorList), true);
    const authorities = inject<ComputedRef<Set<String>>>('authorities', () => computed(() => store.account?.authorities), true);
    const preview = ref(null);
    const deleteAccount = ref(null);
    const passwordConfirm = ref('');
    const deleteError: Ref<boolean> = ref(false);
    const hasAnyAuthorityValues: Ref<any> = ref({});
    const componentKey = ref(new Date().getTime());
    const adminCount = ref<number>(0);
    const onlyOneAdmin = ref<boolean>(true);
    const billingAddressWarning = ref<boolean>(true);

    const isAdmin = computed(() => {
      if (authorities.value && Array.isArray(authorities.value)) {
        return authorities.value.includes('ROLE_ADMIN');
      }
      return false;
    });

    const canDeleteAdmin = () => {
      onlyOneAdmin.value = adminCount.value <= 1;
    };

    const fetchAdminCount = async () => {
      try {
        const response = await axios.get('api/authorities/countAuthorities');
        adminCount.value = response.data;
        canDeleteAdmin();
      } catch (error) {
        console.error('Fehler beim Abrufen der Admin-Anzahl:', error);
      }
    };

    const retrieveSystem = async () => {
      try {
        const res = await systemService().retrieve();
        system.value = res?.data;
      } catch (error) {
        console.error('Fehler beim Abrufen des Systems:', error);
      }
    };

    onMounted(() => {
      fetchAdminCount();
      retrieveSystem();
      checkBillingAddress();
    });

    const validations = {
      settingsAccount: {
        company: {
          name: {
            required: requiredUnless(isAdmin),
            minLength: minLength(1),
            maxLength: maxLength(100),
          },
          billingAddressRow1: {
            required: requiredUnless(isAdmin),
            minLength: minLength(1),
          },
          billingAddressRow2: {},
          billingAddressRow3: {},
          billingAddressRow4: {},
          billingZipCode: {
            required: requiredUnless(isAdmin),
            minLength: minLength(1),
          },
          billingCity: {
            required: requiredUnless(isAdmin),
            minLength: minLength(1),
          },
          description: {
            required: requiredUnless(isAdmin),
            minLength: minLength(10),
            maxLength: maxLength(1024),
          },
          comment: {
            maxLength: maxLength(1024),
          },
          logoUpload: {},
          exhibitorList: {},
        },
        user: {
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
        },
        phoneNumber: {
          required: requiredUnless(isAdmin),
          maxLength: maxLength(20),
        },
      },
      deleteAccount: {
        passwordConfirm: {
          required,
        },
      },
    };

    const v$ = useVuelidate(validations, { settingsAccount: settingsAccount.value, deleteAccount: deleteAccount.value });

    const checkBillingAddress = () => {
      const companyName = v$?.value?.settingsAccount?.company?.name?.$model || settingsAccount.value?.company?.name || '';
      const billingAddressRow1 =
        v$?.value?.settingsAccount?.company?.billingAddressRow1?.$model || settingsAccount.value?.company?.billingAddressRow1 || '';

      if (companyName && billingAddressRow1 && !billingAddressRow1.trim().toLowerCase().includes(companyName.trim().toLowerCase())) {
        billingAddressWarning.value = true;
      } else if (companyName === '' || billingAddressRow1 === '') {
        billingAddressWarning.value = true;
      } else {
        billingAddressWarning.value = false;
      }
    };

    return {
      v$,
      success,
      error,
      errorEmailExists,
      settingsAccount,
      username,
      preview,
      waitingList,
      exhibitorList,
      authorities,
      passwordConfirm,
      deleteAccount,
      deleteError,
      siteUrl,
      alertService,
      company,
      companyService,
      hasAnyAuthorityValues,
      accountService,
      componentKey,
      adminCount,
      onlyOneAdmin,
      system,
      billingAddressWarning,
      checkBillingAddress,
    };
  },
  computed: {
    absoluteImageUrl(): string {
      const logoPath = this.settingsAccount.company?.logo ? this.settingsAccount.company?.logo.replace(/\\/g, '/') : '';
      if (logoPath && !logoPath.startsWith('/')) {
        return `/${logoPath}?key=${this.componentKey}`;
      }

      return logoPath ? `${logoPath}?key=${this.componentKey}` : '';
    },
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
          window.scrollTo(0, 0);
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
    forceRender() {
      this.componentKey = new Date().getTime();
    },
    logoUpload(event: Event): void {
      const input = event.target as HTMLInputElement;
      if (input.files && input.files[0]) {
        const file = input.files[0];
        console.log('Hochgeladene Datei: ', file);

        const reader = new FileReader();
        reader.onload = (e: ProgressEvent<FileReader>) => {
          const fileContent = e.target?.result;

          // convert to base64 string
          const contentBase64 = btoa(fileContent as string);

          console.log('Dateiinhalt:', contentBase64);

          // send file to server
          this.isSaving = true;
          this.companyService()
            .uploadImage(this.settingsAccount.company.id, contentBase64)
            .then(param => {
              this.isSaving = false;
              this.$bvToast.toast('Unternehmenslogo aktualisiert', {
                toaster: 'b-toaster-top-center',
                variant: 'success',
                solid: true,
                autoHideDelay: 5000,
              });
              this.settingsAccount.company.logo = param.logo;
              this.forceRender();
            })
            .catch(error => {
              this.isSaving = false;
              console.log(error.response);
              this.alertService.showHttpError(error.response);
            });
        };
        reader.readAsBinaryString(file);
      }
    },
    showDeleteModal() {
      this.$refs['deleteAcc-modal'].show();
    },
    hideDeleteModal() {
      this.$refs['deleteAcc-modal'].hide();
    },
    resetDeleteModal() {
      this.passwordConfirm = '';
      this.deleteError = false;
    },
    showCancelBooking() {
      this.$refs['cancelBooking-modal'].show();
    },
    hideCancelBooking() {
      this.$refs['cancelBooking-modal'].hide();
    },
    hasAnyAuthority(authorities: any): boolean {
      this.accountService.hasAnyAuthorityAndCheckAuth(authorities).then(value => {
        if (this.hasAnyAuthorityValues[authorities] !== value) {
          this.hasAnyAuthorityValues = { ...this.hasAnyAuthorityValues, [authorities]: value };
        }
      });
      return this.hasAnyAuthorityValues[authorities] ?? false;
    },
    async confirmDelete(id: number) {
      try {
        const response = await axios.delete(`api/account/delete-account/${id}`, {
          data: {
            currentPassword: this.passwordConfirm,
          },
          headers: {
            'Content-Type': 'application/json',
          },
        });
        if (response.status === 200) {
          this.deleteError = false;
          console.log('Account wurde gelöscht');
          sessionStorage.setItem('accountDeleted', 'true');
          this.$router.push({ path: '/' }).then(() => {
            this.$router.go(0);
          });
        }
      } catch (ex) {
        this.deleteError = true;
        console.error('Fehler beim Löschen des Accounts:', ex);
      }
    },
    async removeWaitingList() {
      const response = await axios.put('api/account/remove-waitinglist', this.settingsAccount);
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
    formatDate(dateString: string): string {
      const options: Intl.DateTimeFormatOptions = { day: '2-digit', month: '2-digit', year: 'numeric' };
      return new Date(dateString).toLocaleDateString('de-DE', options);
    },
    calculateCancellationFee(): number {
      var multiplier = 1;
      if (Date.now() <= new Date(this.system.cancellationReimbursementUntil).getTime()) {
        multiplier = (100 - this.system.cancellationReimbursement) / 100;
      }
      return this.settingsAccount.booking.price * multiplier;
    },
    formatCurrency(value: number): string {
      return new Intl.NumberFormat('de-DE', { style: 'currency', currency: 'EUR' }).format(value);
    },
  },
});
