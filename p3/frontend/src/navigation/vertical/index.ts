import communication from './communication'
import customers from './customers'
import promotions from './promotions'
import rewards from './rewards'
import others from './others'
import type { VerticalNavItems } from '@/@layouts/types'

export default [...customers, ...communication, ...promotions, ...rewards, ...others] as VerticalNavItems
