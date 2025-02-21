import { defineComponent, ref, type Ref } from 'vue';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AdminDashboard',
  setup() {
    const value: Ref<Number> = ref(50);
    const max: Ref<Number> = ref(100);
    const items: Ref<Array<any>> = ref([]);

    return {
      value,
      max,
      items,
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init(): void {
      this.items.push({ name: 'Greeting', value: 'Moin World' });
    },
  },
});
