<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bookaboothApp.booth.home.createOrEditLabel" data-cy="BoothCreateUpdateHeading">Booth erstellen oder bearbeiten</h2>
        <div>
          <div class="form-group" v-if="booth.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="booth.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="booth-title"
              data-cy="title"
              :class="{ valid: !v$.title.$invalid, invalid: v$.title.$invalid }"
              v-model="v$.title.$model"
              required
            />
            <div v-if="v$.title.$anyDirty && v$.title.$invalid">
              <small class="form-text text-danger" v-for="error of v$.title.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-ceilingHeight">Ceiling Height</label>
            <input
              type="number"
              class="form-control"
              name="ceilingHeight"
              id="booth-ceilingHeight"
              data-cy="ceilingHeight"
              :class="{ valid: !v$.ceilingHeight.$invalid, invalid: v$.ceilingHeight.$invalid }"
              v-model.number="v$.ceilingHeight.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-available">Available</label>
            <input
              type="checkbox"
              class="form-check"
              name="available"
              id="booth-available"
              data-cy="available"
              :class="{ valid: !v$.available.$invalid, invalid: v$.available.$invalid }"
              v-model="v$.available.$model"
              required
            />
            <div v-if="v$.available.$anyDirty && v$.available.$invalid">
              <small class="form-text text-danger" v-for="error of v$.available.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-location">Location</label>
            <select class="form-control" id="booth-location" data-cy="location" name="location" v-model="booth.location">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="booth.location && locationOption.id === booth.location.id ? booth.location : locationOption"
                v-for="locationOption in locations"
                :key="locationOption.id"
              >
                {{ locationOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label for="booth-servicePackage">Service Package</label>
            <select
              class="form-control"
              id="booth-servicePackages"
              data-cy="servicePackage"
              multiple
              name="servicePackage"
              v-if="booth.servicePackages !== undefined"
              v-model="booth.servicePackages"
            >
              <option
                v-bind:value="getSelected(booth.servicePackages, servicePackageOption, 'id')"
                v-for="servicePackageOption in servicePackages"
                :key="servicePackageOption.id"
              >
                {{ servicePackageOption.id }}
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
<script lang="ts" src="./booth-update.component.ts"></script>
