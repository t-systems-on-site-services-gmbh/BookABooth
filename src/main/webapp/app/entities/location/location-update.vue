<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bookaboothApp.location.home.createOrEditLabel" data-cy="LocationCreateUpdateHeading">Lageplan erstellen oder bearbeiten</h2>
        <div>
          <div class="form-group" v-if="location.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="location.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="location-location">Lokation</label>
            <input
              type="text"
              class="form-control"
              name="location"
              id="location-location"
              data-cy="location"
              :class="{ valid: !v$.location.$invalid, invalid: v$.location.$invalid }"
              v-model="v$.location.$model"
            />
            <div v-if="v$.location.$anyDirty && v$.location.$invalid">
              <small class="form-text text-danger" v-for="error of v$.location.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="location-imageUrl">Grafik</label>
            <input
              type="text"
              class="form-control"
              name="imageUrl"
              id="location-imageUrl"
              data-cy="imageUrl"
              :class="{ valid: !v$.imageUrl.$invalid, invalid: v$.imageUrl.$invalid }"
              v-model="v$.imageUrl.$model"
            />
            <div v-if="v$.location.$anyDirty && v$.location.$invalid">
              <small class="form-text text-danger" v-for="error of v$.location.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
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
<script lang="ts" src="./location-update.component.ts"></script>
