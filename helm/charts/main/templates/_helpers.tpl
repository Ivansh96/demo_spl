{{- define "generic.helmRouteFix" -}}
status:
  ingress: []
{{- end -}}

{{- define "generic.name" -}}
{{- $name := .Values.main.nameOverride | default .Chart.Name -}}

{{- if .ResourceName -}}
{{- $name = printf "%s-%s" $name .ResourceName -}}
{{- end -}}

{{- $name -}}
{{- end }}

{{- define "generic.fullname" -}}

{{- $prefix := .Values.main.fullnameOverride | default .Release.Name -}}
{{- $name := (include "generic.name" .) | trimPrefix (printf "%s%s" $prefix "-") -}}
{{- $name := (include "generic.name" .) -}}

{{- $fullname := (printf "%s-%s" $prefix $name) -}}

{{- if $name | hasPrefix $prefix -}}
{{- $fullname = $name -}}
{{- end -}}

{{- $fullname -}}
{{- end }}

{{- define "generic.fullversion" -}}
{{- printf "%s" .Chart.AppVersion }}
{{- end }}

{{- define "generic.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" }}
{{- end }}

{{- define "generic.labels" -}}
helm.sh/chart: {{ include "generic.chart" . }}
{{ include "generic.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "generic.selectorLabels" -}}
app.kubernetes.io/name: {{ include "generic.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "generic.serviceAccountName" -}}
{{- default (default "default" .Values.global.serviceAccount.name) .Values.main.serviceAccount.name }}
{{- end }}
