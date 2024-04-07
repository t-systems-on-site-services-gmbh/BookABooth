<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bookaboothApp.servicePackage.home.createOrEditLabel" data-cy="ServicePackageCreateUpdateHeading">
          Service Package erstellen oder bearbeiten
        </h2>
        <div>
          <div class="form-group" v-if="servicePackage.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="servicePackage.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="service-package-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="service-package-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="service-package-price">Price</label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="service-package-price"
              data-cy="price"
              :class="{ valid: !v$.price.$invalid, invalid: v$.price.$invalid }"
              v-model.number="v$.price.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="service-package-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="service-package-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
          </div>
          <div class="form-group">
            <label for="service-package-booth">Booth</label>
            <select
              class="form-control"
              id="service-package-booths"
              data-cy="booth"
              multiple
              name="booth"
              v-if="servicePackage.booths !== undefined"
              v-model="servicePackage.booths"
            >
              <option
                v-bind:value="getSelected(servicePackage.booths, boothOption, 'id')"
                v-for="boothOption in booths"
                :key="boothOption.id"
              >
                {{ boothOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Abbrechen</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Speichern</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./service-package-update.component.ts"></script>
