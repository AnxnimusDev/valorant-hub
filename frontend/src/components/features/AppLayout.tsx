import { NavLink, Outlet } from 'react-router-dom'

const NAV_ITEMS = [
  { to: '/', label: 'Dashboard', end: true },
  { to: '/recomendaciones', label: 'Recomendaciones' },
  { to: '/coaching', label: 'Coaching' },
  { to: '/prediccion', label: 'Predicción' },
  { to: '/gamificacion', label: 'Gamificación' },
]

/**
 * Layout compartido de las pantallas autenticadas, calcado del nav-bar del
 * sistema de diseño (design/wireframes/ValorantHub Design System.dc.html).
 * La versión final con avatar/rango real llega en Fase 7.
 */
export function AppLayout() {
  return (
    <div className="min-h-screen bg-bg text-white">
      <header className="sticky top-0 z-10 flex h-16 items-center justify-between border-b border-white/8 bg-surface px-8">
        <div className="flex items-center gap-9">
          <NavLink to="/" className="flex items-center gap-2.5 no-underline">
            <span className="h-4 w-4 rotate-45 rounded-sm bg-primary" />
            <span className="font-display text-[17px] font-extrabold">
              VALORANT<span className="text-primary">HUB</span>
            </span>
          </NavLink>
          <nav className="flex gap-7">
            {NAV_ITEMS.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  `pb-5 text-sm font-semibold no-underline ${
                    isActive
                      ? 'border-b-2 border-primary text-white'
                      : 'text-white/55 hover:text-white'
                  }`
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
        </div>
        <NavLink to="/perfil" className="flex items-center gap-3.5 no-underline">
          <div className="h-8.5 w-8.5 rounded-full border-2 border-primary bg-accent-2" />
        </NavLink>
      </header>

      <main className="mx-auto max-w-6xl px-8 pt-10">
        <Outlet />
      </main>
    </div>
  )
}
