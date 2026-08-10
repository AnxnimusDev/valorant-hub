interface PagePlaceholderProps {
  title: string
  wireframe: string
}

/**
 * Placeholder temporal para pantallas aún no implementadas. Se reemplaza
 * pantalla a pantalla en Fase 7, traduciendo el wireframe .dc.html indicado
 * (design/wireframes/) a componentes reales conectados a la API.
 */
export function PagePlaceholder({ title, wireframe }: PagePlaceholderProps) {
  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center gap-3 text-center">
      <h1 className="font-display text-2xl font-extrabold">{title}</h1>
      <p className="text-sm text-white/50">
        Pendiente de implementar en Fase 7 — wireframe de referencia:{' '}
        <code className="rounded bg-surface px-2 py-1 font-mono text-accent">
          design/wireframes/{wireframe}
        </code>
      </p>
    </div>
  )
}
