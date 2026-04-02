<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="company">
        <h2 class="jh-entity-heading" data-cy="companyDetailsHeading"><span>Firma</span> {{ company.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ company.name }}</span>
          </dd>
          <dt>
            <span>E-Mail</span>
          </dt>
          <dd>
            <span>{{ company.mail }}</span>
          </dd>
          <dt>
            <span>Adresszeile 1</span>
          </dt>
          <dd>
            <span>{{ company.billingAddressRow1 }}</span>
          </dd>
          <dt>
            <span>Adresszeile 2</span>
          </dt>
          <dd>
            <span>{{ company.billingAddressRow2 }}</span>
          </dd>
          <dt>
            <span>Adresszeile 3</span>
          </dt>
          <dd>
            <span>{{ company.billingAddressRow3 }}</span>
          </dd>
          <dt>
            <span>Adresszeile 4</span>
          </dt>
          <dd>
            <span>{{ company.billingAddressRow4 }}</span>
          </dd>
          <dt>
            <span>PLZ</span>
          </dt>
          <dd>
            <span>{{ company.billingZipCode }}</span>
          </dd>
          <dt>
            <span>Ort</span>
          </dt>
          <dd>
            <span>{{ company.billingCity }}</span>
          </dd>
          <dt>
            <span>Logo</span>
          </dt>
          <dd>
            <span>{{ company.logo }}</span>
          </dd>
          <dt>
            <span>Beschreibung</span>
          </dt>
          <dd>
            <span>{{ company.description }}</span>
          </dd>
          <dt>
            <span>Warteliste</span>
          </dt>
          <dd>
            <span>{{ company.waitingList }}</span>
          </dd>
          <dt>
            <span>Ausstellerliste</span>
          </dt>
          <dd>
            <span>{{ company.exhibitorList }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Zurück</span>
        </button>
        <router-link v-if="company.id" :to="{ name: 'CompanyEdit', params: { companyId: company.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Bearbeiten</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./company-details.component.ts"></script>
