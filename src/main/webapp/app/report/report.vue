<template>
  <div>
    <p>Hier kann man den Bericht mit der Information darüber Buchung herunterladen</p>
    <button class="btn btn-primary" @click="downloadPdf">download PDF</button>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  name: 'DownloadPdfButton',
  methods: {
    async downloadPdf() {
      const response = await axios.get('api/bookings/downloadexcel', { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'report.xlsx');
      document.body.appendChild(link);
      link.click();
      link.remove();
    },
  },
};
</script>
