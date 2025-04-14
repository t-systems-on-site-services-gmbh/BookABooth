<template>
  <div>
    <h2>Änderung der Datenschutzerklärung</h2>
    <p class="m-2">
      Bei Aktualisierung der Datenschutzerklärung kann den Benutzern mit dem folgenden Button ein Hinweis über die Aktualisierung angezeigt
      werden. Der Hinweis wird allen Benutzern beim nächsten Login angezeigt.
    </p>
  </div>
  <div v-if="privacyPolicies" class="row col-md-3">
    <table class="table table-striped table-sm" aria-describedby="servicePackages">
      <thead>
        <tr>
          <th scope="row"><span>Datum</span></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="privacyPolicy in privacyPolicies" :key="privacyPolicy.id" data-cy="entityTable">
          <td>{{ formatDate(privacyPolicy.fromDate) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
  <div>
    <button class="btn btn-primary m-2" @click="createNewPrivacyPolicy">Datenschutzerklärung aktualisieren</button>
  </div>
</template>

<script lang="ts" src="./privacy-policy.component.ts"></script>
