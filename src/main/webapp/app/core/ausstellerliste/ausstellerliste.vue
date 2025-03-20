<template>
  <div>
    <h2 id="page-heading" data-cy="AusstellerlisteHeading" class="mb-3">
      <span id="ausstellerliste-heading">Ausstellerliste</span>
    </h2>
    <div v-if="loading" class="d-flex justify-content-center align-items-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden"></span>
      </div>
    </div>
    <div v-else>
      <div class="row">
        <div v-for="company in companies" :key="company.id" class="col-lg-4 mb-4">
          <div class="card shadow-sm company-card">
            <div class="img-container">
              <img
                :src="getLogoUrl(company.logo)"
                v-if="company.logo"
                alt="Firmenlogo"
                class="card-img-top p-3"
                style="object-fit: contain; height: 150px"
              />
              <div v-else class="placeholder" style="height: 150px; display: flex; align-items: center; justify-content: center">
                {{ company.name }}
              </div>
            </div>
            <div class="card-body d-flex flex-column">
              <h5 class="card-title">{{ company.name }}</h5>
              <div class="card-text">
                <div v-if="company.description.length > 50">
                  <p v-if="company.collapsed">{{ company.description.substring(0, 50) }}...</p>
                  <p v-else>{{ company.description }}</p>
                  <button class="btn btn-link p-0" type="button" @click="toggleCollapse(company)">
                    {{ company.collapsed ? 'Mehr anzeigen' : 'Weniger anzeigen' }}
                  </button>
                </div>
                <p v-else>{{ company.description }}</p>
              </div>
              <p class="card-text mt-3"><strong>Stand:</strong> {{ company.booth }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./ausstellerliste.component.ts"></script>

<style scoped>
.img-container {
  background-color: #f8f9fa;
  border-bottom: 1px solid #ddd;
}
</style>
