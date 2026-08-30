"use client";

import { useEffect, type ReactNode } from "react";
import { Icon } from "./Icon";

interface ModalProps {
  title: string;
  eyebrow?: string;
  children: ReactNode;
  onClose: () => void;
  wide?: boolean;
}

export function Modal({
  title,
  eyebrow,
  children,
  onClose,
  wide = false,
}: ModalProps) {
  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") onClose();
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [onClose]);

  return (
    <div className="modalBackdrop" role="presentation" onMouseDown={onClose}>
      <section
        aria-modal="true"
        aria-labelledby="modal-title"
        className={`modalCard${wide ? " modalCardWide" : ""}`}
        role="dialog"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <header className="modalHeader">
          <div>
            {eyebrow && <p className="eyebrow">{eyebrow}</p>}
            <h2 id="modal-title">{title}</h2>
          </div>
          <button className="iconButton" type="button" onClick={onClose}>
            <Icon name="close" width="22" height="22" />
            <span className="srOnly">بستن</span>
          </button>
        </header>
        {children}
      </section>
    </div>
  );
}
