<template>
  <div>
    <h2 id="page-heading" data-cy="BoothHeading">
      <span id="booth-heading">Stand Buchen</span>
    </h2>

    <label for="location" class="mt-3">Ort</label>
    <select id="location" class="custom-select mb-3" v-model="selectedLocation">
      <option value="">Alle</option>
      <option v-for="location in locations" :key="location.id" :value="location">{{ location.location }}</option>
    </select>

    <div v-if="selectedLocation">
      <img :src="selectedLocation.imageUrl" style="width: 100%" />
    </div>

    <br />
    <div class="alert alert-warning" v-if="!isFetching && booths && booths.length === 0">
      <span>Keine Stände gefunden</span>
    </div>
    <div class="table-responsive" v-if="booths && booths.length > 0">
      <table class="table table-striped" aria-describedby="booths">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Name</span></th>
            <th scope="row"><span>Deckenhöhe</span></th>
            <th scope="row"><span>Verfügbar</span></th>
            <th scope="row"><span>Ort</span></th>
            <th scope="row"><span>Service Paket</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="booth in filteredBooths.filter(booth => booth.available === true)" :key="booth.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'BoothView', params: { boothId: booth.id } }">{{ booth.id }}</router-link>
            </td>
            <td>{{ booth.title }}</td>
            <td>{{ booth.ceilingHeight }}</td>
            <td>{{ booth.available }}</td>
            <td>
              <div v-if="booth.location">
                <router-link :to="{ name: 'LocationView', params: { locationId: booth.location.id } }">{{
                  locations.find(l => l.id == booth.location.id)?.location
                }}</router-link>
              </div>
            </td>
            <td>
              <span v-for="(servicePackage, i) in booth.servicePackages" :key="servicePackage.id"
                >{{ i > 0 ? ', ' : '' }}
                <router-link
                  class="form-control-static"
                  :to="{ name: 'ServicePackageView', params: { servicePackageId: servicePackage.id } }"
                  >{{ servicePackages.find(sp => sp.id == servicePackage.id)?.name }}</router-link
                >
              </span>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <button @click="showConfirmationModal(booth)" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                  <font-awesome-icon icon="store"></font-awesome-icon>
                  <span class="d-none d-md-inline">Stand buchen</span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
  <b-modal ref="confirmation-modal" hide-footer title="Buchung bestätigen" @hidden="resetConfirmationModal">
    <div class="d-block text-left">
      <p>Sind Sie sich sicher, dass Sie diesen Stand kostenpflichtig buchen wollen?</p>
      <p>
        Stand: {{ selectedBooth.title }}<br />
        Kosten: {{ calculatePrice(selectedBooth) }} €<br />
      </p>
      <p>Bei Bestätigung der Buchung erkennen Sie die <a href="#todo">Ausstellerbedingungen</a> an.</p>
    </div>
    <div class="d-flex justify-content-end">
      <b-button class="btn btn-secondary" @click="abortBooking(selectedBooth.id)">Abbrechen</b-button>
      <b-button type="submit" class="btn btn-success ml-3" id="confirmBooking" @click="confirmBooking(selectedBooth.id)"
        >Buchung bestätigen</b-button
      >
    </div>
  </b-modal>
</template>

<script lang="ts" src="./bookabooth.component.ts"></script>
