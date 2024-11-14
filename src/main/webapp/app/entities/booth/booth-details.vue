<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="booth">
        <h2 class="jh-entity-heading" data-cy="boothDetailsHeading"><span>Stand</span> {{ booth.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ booth.title }}</span>
          </dd>
          <dt>
            <span>Deckenhöhe</span>
          </dt>
          <dd>
            <span>{{ booth.ceilingHeight }}</span>
          </dd>
          <dt>
            <span>Verfügbar</span>
          </dt>
          <dd>
            <span>{{ booth.available }}</span>
          </dd>
          <dt>
            <span>Ort</span>
          </dt>
          <dd>
            <div v-if="booth.location">
              <router-link :to="{ name: 'LocationView', params: { locationId: booth.location.id } }">{{
                locations.find(l => l.id == booth.location.id)?.location
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Service Paket</span>
          </dt>
          <dd>
            <span v-for="(servicePackage, i) in booth.servicePackages" :key="servicePackage.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'ServicePackageView', params: { servicePackageId: servicePackage.id } }">{{
                servicePackages.find(sp => sp.id == servicePackage.id)?.name
              }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Zurück</span>
        </button>
        <router-link v-if="booth.id" :to="{ name: 'BoothEdit', params: { boothId: booth.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Bearbeiten</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./booth-details.component.ts"></script>
