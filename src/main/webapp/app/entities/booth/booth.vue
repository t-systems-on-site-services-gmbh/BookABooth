<template>
  <div>
    <h2 id="page-heading" data-cy="BoothHeading">
      <span id="booth-heading">Booths</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Liste aktualisieren</span>
        </button>
        <router-link :to="{ name: 'BoothCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-booth"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Booth erstellen</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && booths && booths.length === 0">
      <span>Keine Booths gefunden</span>
    </div>
    <div class="table-responsive" v-if="booths && booths.length > 0">
      <table class="table table-striped" aria-describedby="booths">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Title</span></th>
            <th scope="row"><span>Ceiling Height</span></th>
            <th scope="row"><span>Available</span></th>
            <th scope="row"><span>Location</span></th>
            <th scope="row"><span>Service Package</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="booth in booths" :key="booth.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'BoothView', params: { boothId: booth.id } }">{{ booth.id }}</router-link>
            </td>
            <td>{{ booth.title }}</td>
            <td>{{ booth.ceilingHeight }}</td>
            <td>{{ booth.available }}</td>
            <td>
              <div v-if="booth.location">
                <router-link :to="{ name: 'LocationView', params: { locationId: booth.location.id } }">{{ booth.location.id }}</router-link>
              </div>
            </td>
            <td>
              <span v-for="(servicePackage, i) in booth.servicePackages" :key="servicePackage.id"
                >{{ i > 0 ? ', ' : '' }}
                <router-link
                  class="form-control-static"
                  :to="{ name: 'ServicePackageView', params: { servicePackageId: servicePackage.id } }"
                  >{{ servicePackage.id }}</router-link
                >
              </span>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'BoothView', params: { boothId: booth.id } }"
                  class="btn btn-info btn-sm details"
                  data-cy="entityDetailsButton"
                >
                  <font-awesome-icon icon="eye"></font-awesome-icon>
                  <span class="d-none d-md-inline">Details</span>
                </router-link>
                <router-link
                  :to="{ name: 'BoothEdit', params: { boothId: booth.id } }"
                  class="btn btn-primary btn-sm edit"
                  data-cy="entityEditButton"
                >
                  <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                  <span class="d-none d-md-inline">Bearbeiten</span>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(booth)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Löschen</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="bookaboothApp.booth.delete.question" data-cy="boothDeleteDialogHeading">Löschen bestätigen</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-booth-heading">Soll Booth {{ removeId }} wirklich dauerhaft gelöscht werden?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Abbrechen</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-booth"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeBooth()"
          >
            Löschen
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./booth.component.ts"></script>
