<template>
  <div>
    <h2 id="page-heading" data-cy="BoothHeading">
      <span id="booth-heading">Stand buchen</span>
    </h2>

    <div v-if="!isFetching && !system.enabled" class="alert alert-danger">Die Standbuchung ist systemseitig nicht freigegeben.</div>
    <div v-else-if="!isFetching && !isBookingAllowed" class="alert alert-danger">
      Es fehlen Daten von Ihnen. Prüfen Sie die Checkliste auf der <a href="/">Startseite</a>.
    </div>
    <div v-if="boothId !== null && boothId > 0" class="alert alert-success">
      Sie haben Stand {{ booths.find(b => b.id === boothId)?.title }} gebucht.
      <span v-if="myBooking != null">
        <br />Kosten: {{ formatCurrency(myBooking.price) }} <br />Buchungsdatum: {{ formatDate(myBooking.confirmed) }}
      </span>
    </div>
    <br />
    <div v-if="filteredServicePackages" class="row col-md-3">
      <table class="table table-striped table-sm" aria-describedby="servicePackages">
        <thead>
          <tr>
            <th scope="row"><span>Service Paket</span></th>
            <th scope="row"><span>Preis</span></th>
            <th scope="row"><span>Beschreibung</span></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="servicePackage in filteredServicePackages" :key="servicePackage.id" data-cy="entityTable">
            <td>{{ servicePackage.name }}</td>
            <td>{{ formatCurrency(servicePackage.price) }}</td>
            <td>{{ servicePackage.description }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <label for="location" class="mt-3">Ort</label>
    <select id="location" class="custom-select mb-3" v-model="selectedLocation">
      <option value="">Alle</option>
      <option v-for="location in locations" :key="location.id" :value="location">{{ location.location }}</option>
    </select>
    <br />
    <div class="row">
      <b-modal size="lg" ref="lageplan-modal" hide-footer title="Lageplan">
        <div class="col-12 mb-3 mb-md-0" v-if="selectedLocation">
          <img :src="selectedLocation.imageUrl" width="100%" />
        </div>
        <div class="d-flex justify-content-end">
          <b-button type="submit" class="btn btn-success ml-3" id="confirmLageplan" @click="hideLageplanModal()"> Ok </b-button>
        </div>
      </b-modal>
      <div class="col-12 col-md-3 mb-3 mb-md-0" v-if="selectedLocation">
        <img :src="selectedLocation.imageUrl" width="100%" @click="showLageplanModal()" />
      </div>

      <br />
      <div class="alert alert-warning" v-if="!isFetching && booths && booths.length === 0">
        <span>Keine Stände gefunden</span>
      </div>
      <div class="table-responsive col-md-9" v-if="booths && booths.length > 0">
        <table class="table table-striped" aria-describedby="booths">
          <thead>
            <tr>
              <th scope="row"><span>Name</span></th>
              <th scope="row"><span>Deckenhöhe in m</span></th>
              <th scope="row"><span>Ort</span></th>
              <th scope="row"><span>Service Pakete</span></th>
              <th scope="row"><span>Firma</span></th>
              <th scope="row"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="booth in filteredBooths" :key="booth.id" data-cy="entityTable" :class="booth.id === boothId ? 'bg-warning' : ''">
              <td>{{ booth.title }}</td>
              <td>{{ booth.ceilingHeight }}</td>
              <td>
                <div v-if="booth.location">
                  {{ locations.find(l => l.id == booth.location.id)?.location }}
                </div>
              </td>
              <td>
                <span v-for="(servicePackage, i) in booth.servicePackages" :key="servicePackage.id"
                  >{{ i > 0 ? ', ' : '' }}
                  {{ servicePackages.find(sp => sp.id == servicePackage.id)?.name }}
                </span>
              </td>
              <td>{{ booth.companyName }}</td>
              <td class="text-right">
                <div class="btn-group">
                  <button
                    @click="displayConfirmationModal(booth)"
                    class="btn btn-primary btn-sm edit"
                    data-cy="entityEditButton"
                    :disabled="
                      !booth.available ||
                      unavailableBooths?.find(b => b.id === booth.id) ||
                      !isBookingAllowed ||
                      (boothId != null && boothId > 0)
                    "
                  >
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
  </div>
  <b-modal ref="confirmation-modal" hide-footer title="Buchung bestätigen" @hidden="resetConfirmationModal">
    <div class="d-block text-left">
      <p>Sind Sie sich sicher, dass Sie diesen Stand kostenpflichtig buchen wollen?</p>
      <p>
        Stand: {{ selectedBooth.title }}<br />
        Kosten: {{ formatCurrency(calculatePrice(selectedBooth)) }}<br />
      </p>
      <p>
        Es fallen Stornogebühren in Höhe von {{ 100 - system.cancellationReimbursement }}% des Buchungspreises an. Bei einer Stornierung
        nach dem {{ formatDate(system.cancellationReimbursementUntil) }} wird der gesamte Betrag fällig.
      </p>
      <p class="m-4">
        <input class="form-check-input" type="checkbox" id="confirmConditions" v-model="v$.confirmConditions.$model" />
        <label class="form-check-label" for="confirmConditions"
          >Ich habe die <b-link @click="showInfoModal()">Ausstellerbedingungen</b-link> gelesen und akzeptiert.</label
        >
      </p>
    </div>
    <div class="d-flex justify-content-end">
      <b-button class="btn btn-secondary" @click="abortBooking(currentBooking.id)">Abbrechen</b-button>
      <b-button
        :disabled="!v$.confirmConditions.$model"
        type="submit"
        class="btn btn-success ml-3"
        id="confirmBooking"
        @click="confirmBooking(currentBooking.id)"
      >
        Buchung bestätigen
      </b-button>
    </div>
  </b-modal>
  <b-modal size="xl" ref="ausstellerinfo-modal" hide-footer title="Ausstellerbedingungen">
    <div class="d-block text-left">
      <ausstellerinfo></ausstellerinfo>
    </div>
    <div class="d-flex justify-content-end">
      <b-button type="submit" class="btn btn-success ml-3" id="confirmInfo" @click="hideInfoModal()"> Ok </b-button>
    </div>
  </b-modal>
</template>

<script lang="ts" src="./bookabooth.component.ts"></script>
