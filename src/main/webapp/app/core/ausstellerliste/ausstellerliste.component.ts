import { defineComponent, onMounted, ref } from 'vue';
import AusstellerService from './ausstellerliste.service';

export default defineComponent({
  name: 'Ausstellerliste',
  setup() {
    const companies = ref([]);
    const loading = ref(true);
    const ausstellerService = new AusstellerService();

    const loadCompanies = async () => {
      loading.value = true;
      try {
        const data = await ausstellerService.getPublicCompanies();
        companies.value = data.map(company => ({
          ...company,
          booth: company.boothTitle || '',
          collapsed: true,
        }));
      } catch (error) {
        console.error('Fehler beim Laden der Daten:', error);
      } finally {
        loading.value = false;
      }
    };

    const toggleCollapse = (company: { collapsed: boolean }) => {
      company.collapsed = !company.collapsed;
    };

    const getLogoUrl = (logo: any) => {
      return logo ? `${logo}?${new Date().getTime()}` : '';
    };

    onMounted(() => {
      loadCompanies();
    });

    return {
      companies,
      loading,
      toggleCollapse,
      getLogoUrl,
    };
  },
});
