<template>
  <div class="table-responsive">
    <b-progress :max="max" height="2rem">
      <b-progress-bar :value="value">
        <span
          >Zelt 1: <strong>{{ value.toFixed(2) }} / {{ max }}</strong></span
        >
      </b-progress-bar>
    </b-progress>
    <b-progress :max="max" height="2rem">
      <b-progress-bar :value="value">
        <span
          >Zelt 2: <strong>{{ value.toFixed(2) }} / {{ max }}</strong></span
        >
      </b-progress-bar>
    </b-progress>
    <b-progress :max="max" height="2rem">
      <b-progress-bar :value="value">
        <span
          >Aula: <strong>{{ value.toFixed(2) }} / {{ max }}</strong></span
        >
      </b-progress-bar>
    </b-progress>

    <div>
      <b-table striped hover :items="items"></b-table>
    </div>
  </div>

  <Button class="btn btn-primary">Rechnungsdaten</Button>
</template>

<script lang="ts" src="./admin-dashboard.component.ts"></script>
