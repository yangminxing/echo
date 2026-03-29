export interface CaptchaData {
  text: string
  dataUrl: string
}

export function generateCaptcha(width: number = 120, height: number = 40): CaptchaData {
  const canvas = document.createElement('canvas')
  canvas.width = width
  canvas.height = height
  const ctx = canvas.getContext('2d')!

  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789'
  let captchaText = ''

  for (let i = 0; i < 4; i++) {
    captchaText += chars.charAt(Math.floor(Math.random() * chars.length))
  }

  ctx.fillStyle = '#f0f0f0'
  ctx.fillRect(0, 0, width, height)

  ctx.font = 'bold 24px Arial'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'

  for (let i = 0; i < captchaText.length; i++) {
    const x = (width / 5) * (i + 1)
    const y = height / 2 + (Math.random() - 0.5) * 10
    const angle = (Math.random() - 0.5) * 0.4

    ctx.save()
    ctx.translate(x, y)
    ctx.rotate(angle)

    const hue = Math.floor(Math.random() * 60) + 200
    ctx.fillStyle = `hsl(${hue}, 70%, 40%)`
    ctx.fillText(captchaText[i], 0, 0)
    ctx.restore()
  }

  for (let i = 0; i < 5; i++) {
    ctx.strokeStyle = `rgba(100, 100, 100, ${Math.random() * 0.3})`
    ctx.beginPath()
    ctx.moveTo(Math.random() * width, Math.random() * height)
    ctx.lineTo(Math.random() * width, Math.random() * height)
    ctx.stroke()
  }

  for (let i = 0; i < 30; i++) {
    ctx.fillStyle = `rgba(0, 0, 0, ${Math.random() * 0.1})`
    ctx.beginPath()
    ctx.arc(Math.random() * width, Math.random() * height, Math.random() * 1.5, 0, Math.PI * 2)
    ctx.fill()
  }

  return {
    text: captchaText,
    dataUrl: canvas.toDataURL()
  }
}