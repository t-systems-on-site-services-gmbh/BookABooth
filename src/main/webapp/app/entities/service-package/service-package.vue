<template>
  <div>
    <h2 id="page-heading" data-cy="ServicePackageHeading">
      <span id="service-package-heading">Service Packages</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Liste aktualisieren</span>
        </button>
        <router-link :to="{ name: 'ServicePackageCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-service-package"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Service Package erstellen</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && servicePackages && servicePackages.length === 0">
      <span>Keine Service Packages gefunden</span>
    </div>
    <div class="table-responsive" v-if="servicePackages && servicePackages.length > 0">
      <table class="table table-striped" aria-describedby="servicePackages">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Name</span></th>
            <th scope="row"><span>Price</span></th>
            <th scope="row"><span>Description</span></th>
            <th scope="row"><span>Booth</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="servicePackage in servicePackages" :key="servicePackage.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ServicePackageView', params: { servicePackageId: servicePackage.id } }">{{
                servicePackage.id
              }}</router-link>
            </td>
            <td>{{ servicePackage.name }}</td>
            <td>{{ servicePackage.price }}</td>
            <td>{{ servicePackage.description }}</td>
            <td>
              <span v-for="(booth, i) in servicePackage.booths" :key="booth.id"
                >{{ i > 0 ? ', ' : '' }}
                <router-link class="form-control-static" :to="{ name: 'BoothView', params: { boothId: booth.id } }">{{
                  booth.id
                }}</router-link>
              </span>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'ServicePackageView', params: { servicePackageId: servicePackage.id } }"
                  class="btn btn-info btn-sm details"
                  data-cy="entityDetailsButton"
                >
                  <font-awesome-icon icon="eye"></font-awesome-icon>
                  <span class="d-none d-md-inline">Details</span>
                </router-link>
                <router-link
                  :to="{ name: 'ServicePackageEdit', params: { servicePackageId: servicePackage.id } }"
                  class="btn btn-primary btn-sm edit"
                  data-cy="entityEditButton"
                >
                  <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                  <span class="d-none d-md-inline">Bearbeiten</span>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(servicePackage)"
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
        <span id="bookaboothApp.servicePackage.delete.question" data-cy="servicePackageDeleteDialogHeading">Löschen bestätigen</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-servicePackage-heading">Soll Service Package {{ removeId }} wirklich dauerhaft gelöscht werden?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Abbrechen</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-servicePackage"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeServicePackage()"
          >
            Löschen
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./service-package.component.ts"></script>
