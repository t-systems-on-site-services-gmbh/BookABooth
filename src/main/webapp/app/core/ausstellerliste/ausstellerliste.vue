<template>
  <div class="container mt-5">
    <h1 class="text-center mb-4">Ausstellerliste</h1>
    <div v-if="loading" class="d-flex justify-content-center align-items-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Lade Daten...</span>
      </div>
    </div>
    <div v-else>
      <div class="row">
        <div v-for="company in companies" :key="company.id" class="col-md-6 col-lg-4 mb-4">
          <div class="card shadow-sm h-100">
            <img :src="company.logoUrl" alt="Firmenlogo" class="card-img-top p-3" style="object-fit: contain; height: 150px" />
            <div class="card-body d-flex flex-column">
              <h5 class="card-title">{{ company.name }}</h5>
              <p class="card-text">{{ company.description }}</p>
              <p class="card-text"><strong>Stand:</strong> {{ company.booth }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./ausstellerliste.component.ts"></script>

<style scoped>
.card-img-top {
  background-color: #f8f9fa;
  border-bottom: 1px solid #ddd;
}
</style>
